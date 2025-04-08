import React, { useState, useEffect } from 'react';
import { getUsers, updateUser } from '../lib/api';
import { User } from '../lib/types';

export default function UsersPage() {
    const [users, setUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [editingUser, setEditingUser] = useState<User | null>(null);

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        setLoading(true);
        setError('');
        try {
            const usersData = await getUsers();
            if (usersData) {
                setUsers(usersData);
            } else {
                setError('Не удалось загрузить пользователей');
            }
        } catch (err) {
            setError('Ошибка при загрузке пользователей');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleEditClick = (user: User) => {
        setEditingUser({ ...user });
    };

    const handleEditChange = (e: React.ChangeEvent<HTMLInputElement>, field: keyof User) => {
        if (editingUser) {
            setEditingUser({
                ...editingUser,
                [field]: e.target.value
            });
        }
    };

    const handleSaveClick = async () => {
        if (editingUser) {
            setLoading(true);
            try {
                const success = await updateUser(editingUser);
                if (success) {
                    setUsers(users.map(user =>
                        user.code === editingUser.code ? editingUser : user
                    ));
                    setEditingUser(null);
                } else {
                    setError('Не удалось обновить пользователя');
                }
            } catch (err) {
                setError('Ошибка при обновлении пользователя');
                console.error(err);
            } finally {
                setLoading(false);
            }
        }
    };

    const handleCancelClick = () => {
        setEditingUser(null);
    };

    const formatPhoneNumber = (phone: string) => {
        if (!phone) return '';
        const cleaned = ('' + phone).replace(/\D/g, '');
        const match = cleaned.match(/^(\d{1})(\d{3})(\d{3})(\d{2})(\d{2})$/);
        if (match) {
            return `+${match[1]}(${match[2]})${match[3]}-${match[4]}-${match[5]}`;
        }
        return phone;
    };

    return (
        <div className="container mx-auto px-4 py-8">
            <h1 className="text-2xl font-bold mb-6">Список пользователей</h1>

            {error && (
                <div className="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded">
                    {error}
                </div>
            )}

            {loading ? (
                <p>Загрузка...</p>
            ) : (
                <div className="overflow-x-auto">
                    <table className="min-w-full bg-white">
                        <thead>
                        <tr>
                            <th className="py-2 px-4 border-b border-gray-200 bg-gray-50 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Код</th>
                            <th className="py-2 px-4 border-b border-gray-200 bg-gray-50 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">ФИО</th>
                            <th className="py-2 px-4 border-b border-gray-200 bg-gray-50 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Username</th>
                            <th className="py-2 px-4 border-b border-gray-200 bg-gray-50 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Телефон</th>
                            <th className="py-2 px-4 border-b border-gray-200 bg-gray-50 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Кэшбэк</th>
                            <th className="py-2 px-4 border-b border-gray-200 bg-gray-50 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Действия</th>
                        </tr>
                        </thead>
                        <tbody>
                        {users.map(user => (
                            <tr key={user.code} className="hover:bg-gray-50">
                                <td className="py-2 px-4 border-b border-gray-200">{user.code}</td>
                                <td className="py-2 px-4 border-b border-gray-200">
                                    {editingUser?.code === user.code ? (
                                        <input
                                            type="text"
                                            value={editingUser.name}
                                            onChange={(e) => handleEditChange(e, 'name')}
                                            className="w-full px-2 py-1 border rounded"
                                        />
                                    ) : (
                                        user.name
                                    )}
                                </td>
                                <td className="py-2 px-4 border-b border-gray-200">@{user.username}</td>
                                <td className="py-2 px-4 border-b border-gray-200">
                                    {editingUser?.code === user.code ? (
                                        <input
                                            type="tel"
                                            value={editingUser.phoneNumber}
                                            onChange={(e) => handleEditChange(e, 'phoneNumber')}
                                            className="w-full px-2 py-1 border rounded"
                                        />
                                    ) : (
                                        formatPhoneNumber(user.phoneNumber)
                                    )}
                                </td>
                                <td className="py-2 px-4 border-b border-gray-200">
                                    {editingUser?.code === user.code ? (
                                        <input
                                            type="number"
                                            value={editingUser.cashback}
                                            onChange={(e) => handleEditChange(e, 'cashback')}
                                            className="w-full px-2 py-1 border rounded"
                                            step="0.01"
                                            min="0"
                                        />
                                    ) : (
                                        user.cashback.toFixed(2)
                                    )}
                                </td>
                                <td className="py-2 px-4 border-b border-gray-200">
                                    {editingUser?.code === user.code ? (
                                        <div className="flex space-x-2">
                                            <button
                                                onClick={handleSaveClick}
                                                disabled={loading}
                                                className="px-3 py-1 bg-green-500 text-white rounded hover:bg-green-600 disabled:opacity-50"
                                            >
                                                Сохранить
                                            </button>
                                            <button
                                                onClick={handleCancelClick}
                                                className="px-3 py-1 bg-gray-500 text-white rounded hover:bg-gray-600"
                                            >
                                                Отмена
                                            </button>
                                        </div>
                                    ) : (
                                        <button
                                            onClick={() => handleEditClick(user)}
                                            className="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
                                        >
                                            Редактировать
                                        </button>
                                    )}
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}