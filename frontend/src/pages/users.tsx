import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { getUsers, editUser } from '../lib/api';
import { User } from '../lib/types';
import styles from '../styles/users.module.css';
import Head from "next/head";
import { AxiosError } from "axios";
import {getFormattedPhone} from "./user/[code]";

export default function UsersPage() {
    const router = useRouter();
    const [users, setUsers] = useState<User[]>([]);
    const [filteredUsers, setFilteredUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    //const [editingUser, setEditingUser] = useState<User | null>(null);
    const [currentPage, setCurrentPage] = useState(1);
    const [searchTerm, setSearchTerm] = useState('');
    const [searchField, setSearchField] = useState<keyof User>('code');
    const usersPerPage = 5;

    useEffect(() => {
        fetchUsers();
    }, []);

    useEffect(() => {
        filterUsers();
    }, [users, searchTerm, searchField]);

    const fetchUsers = async () => {
        setLoading(true);
        setError('');
        try {
            await getUsers()
                .then( function (usersData) {
                    if (usersData) {
                        setUsers(usersData);
                    } else {
                        setError('Не удалось загрузить пользователей');
                    }
                })
                .catch(function (error) {
                    if (error instanceof AxiosError && error.response.status == 500)
                        setError('Соединение с сервером не установлено');
                    else
                        setError('Неизвестная ошибка');
                });

        } catch (err) {
            setError('Ошибка при загрузке пользователей');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const filterUsers = () => {
        if (!searchTerm) {
            setFilteredUsers(users);
            return;
        }

        const filtered = users.filter(user => {
            const fieldValue = String(user[searchField]).toLowerCase();
            const searchValue = searchTerm.toLowerCase();

            if (searchField === 'phoneNumber') {
                const phoneDigits = user.phoneNumber.replace(/\D/g, '');
                return phoneDigits.includes(searchTerm.replace(/\D/g, ''));
            }

            return fieldValue.includes(searchValue);
        });

        setFilteredUsers(filtered);
        setCurrentPage(1);
    };

    // const handleEditClick = (user: User) => {
    //     setEditingUser({ ...user });
    // };
    //
    // const handleEditChange = (e: React.ChangeEvent<HTMLInputElement>, field: keyof User) => {
    //     if (editingUser) {
    //         setEditingUser({
    //             ...editingUser,
    //             [field]: e.target.value
    //         });
    //     }
    // };
    //
    // // Обработчик изменения телефонного номера
    // const handlePhoneChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    //     setEditingUser({
    //         ...editingUser,
    //         phoneNumber: getFormattedPhone(e)
    //     });
    // };
    //
    // const handleSaveClick = async () => {
    //     if (editingUser) {
    //         setLoading(true);
    //         try {
    //             const success = await editUser(editingUser);
    //             if (success) {
    //                 setUsers(users.map(user =>
    //                     user.code === editingUser.code ? editingUser : user
    //                 ));
    //                 setEditingUser(null);
    //             } else {
    //                 setError('Не удалось обновить пользователя');
    //             }
    //         } catch (err) {
    //             setError('Ошибка при обновлении пользователя');
    //             console.error(err);
    //         } finally {
    //             setLoading(false);
    //         }
    //     }
    // };
    //
    // const handleCancelClick = () => {
    //     setEditingUser(null);
    // };
    // const formatPhoneNumber = (phone: string) => {
    //     if (!phone) return '';
    //     const cleaned = ('' + phone).replace(/\D/g, '');
    //     const match = cleaned.match(/^(\d{1})(\d{3})(\d{3})(\d{2})(\d{2})$/);
    //     if (match) {
    //         return `+${match[1]} (${match[2]}) ${match[3]}-${match[4]}-${match[5]}`;
    //     }
    //     return phone;
    // };

    // Navigation functions
    const navigateToHome = () => {
        router.push('/');
    };

    const navigateToUser = (code: number) => {
        router.push(`/user/${code}`);
    };

    // Pagination logic
    const indexOfLastUser = currentPage * usersPerPage;
    const indexOfFirstUser = indexOfLastUser - usersPerPage;
    const currentUsers = filteredUsers.slice(indexOfFirstUser, indexOfLastUser);
    const totalPages = Math.ceil(filteredUsers.length / usersPerPage);

    const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

    return (
        <div>
            <Head>
                <title>Таблица пользователей</title>
                <meta name="description" content="Таблица пользователей" />
                <link rel="icon" href="/favicon.png" />
            </Head>
            <div className={styles.pageContainer}>
                <div className={styles.header}>
                    <button
                        onClick={navigateToHome}
                        className={styles.backButton}
                    >
                        На главную
                    </button>
                    <h1 className={styles.title}>Список пользователей</h1>
                </div>

                {error && <div className={styles.errorMessage}>{error}</div>}

                <div className={styles.controls}>
                    <div className={styles.searchContainer}>
                        <select
                            value={searchField}
                            onChange={(e) => setSearchField(e.target.value as keyof User)}
                            className={styles.searchSelect}
                        >
                            <option value="code">Код</option>
                            <option value="name">ФИ</option>
                            <option value="username">Никнейм</option>
                            <option value="phoneNumber">Телефон</option>
                        </select>
                        <input
                            type="text"
                            placeholder={`Поиск по ${getFieldLabel(searchField)}...`}
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className={styles.searchInput}
                        />
                    </div>
                </div>

                {loading ? (
                    <div className={styles.loading}>Загрузка...</div>
                ) : (
                    <>
                        <div className={styles.tableContainer}>
                            <table className={styles.table}>
                                <thead>
                                <tr>
                                    <th className={styles.th}>Код</th>
                                    <th className={styles.th}>ФИ</th>
                                    <th className={styles.th}>Username</th>
                                    <th className={styles.th}>Телефон</th>
                                    <th className={styles.th}>Кэшбэк</th>
                                    {/*<th className={styles.th}>Действия</th>*/}
                                </tr>
                                </thead>
                                <tbody>
                                {currentUsers.length > 0 ? (
                                    currentUsers.map(user => (
                                        <tr key={user.code} className={styles.tr}>
                                            <td
                                                className={`${styles.td} ${styles.clickable}`}
                                                onClick={() => navigateToUser(user.code)}
                                            >
                                                {user.code}
                                            </td>
                                            <td className={styles.td}>
                                                {user.name || '-'}
                                            </td>
                                            <td className={styles.td}>
                                                {user.username}
                                            </td>
                                            <td className={styles.td}>
                                                {getFormattedPhone(user.phoneNumber) || '-'}
                                            </td>
                                            <td className={styles.td}>
                                                {user.cashback}
                                            </td>
                                            {/*<td className={styles.td}>*/}
                                            {/*    {editingUser?.code === user.code ? (*/}
                                            {/*        <div className={styles.editButtons}>*/}
                                            {/*            <button*/}
                                            {/*                onClick={handleSaveClick}*/}
                                            {/*                disabled={loading}*/}
                                            {/*                className={`${styles.button} ${styles.saveButton}`}*/}
                                            {/*            >*/}
                                            {/*                Сохранить*/}
                                            {/*            </button>*/}
                                            {/*            <button*/}
                                            {/*                onClick={handleCancelClick}*/}
                                            {/*                className={`${styles.button} ${styles.cancelButton}`}*/}
                                            {/*            >*/}
                                            {/*                Отмена*/}
                                            {/*            </button>*/}
                                            {/*        </div>*/}
                                            {/*    ) : (*/}
                                            {/*        <button*/}
                                            {/*            onClick={() => handleEditClick(user)}*/}
                                            {/*            className={`${styles.button} ${styles.editButton}`}*/}
                                            {/*        >*/}
                                            {/*            Редактировать*/}
                                            {/*        </button>*/}
                                            {/*    )}*/}
                                            {/*</td>*/}
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan={6} className={styles.noResults}>
                                            Пользователи не найдены
                                        </td>
                                    </tr>
                                )}
                                </tbody>
                            </table>
                        </div>

                        {filteredUsers.length > usersPerPage && (
                            <div className={styles.pagination}>
                                <button
                                    onClick={() => paginate(currentPage > 1 ? currentPage - 1 : 1)}
                                    disabled={currentPage === 1}
                                    className={`${styles.pageButton} ${currentPage === 1 ? styles.disabled : ''}`}
                                >
                                    Назад
                                </button>

                                {Array.from({ length: totalPages }, (_, i) => i + 1).map(number => (
                                    <button
                                        key={number}
                                        onClick={() => paginate(number)}
                                        className={`${styles.pageButton} ${currentPage === number ? styles.active : ''}`}
                                    >
                                        {number}
                                    </button>
                                ))}

                                <button
                                    onClick={() => paginate(currentPage < totalPages ? currentPage + 1 : totalPages)}
                                    disabled={currentPage === totalPages}
                                    className={`${styles.pageButton} ${currentPage === totalPages ? styles.disabled : ''}`}
                                >
                                    Вперед
                                </button>
                            </div>
                        )}
                    </>
                )}
            </div>
        </div>
    );
}

function getFieldLabel(field: keyof User): string {
    switch (field) {
        case 'code': return 'коду';
        case 'name': return 'ФИ';
        case 'username': return 'никнейму';
        case 'phoneNumber': return 'номеру телефона';
        default: return '';
    }
}