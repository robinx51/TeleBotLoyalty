import React, {useEffect, useState} from 'react';
import { useRouter } from 'next/router';
import Head from "next/head";
import styles from '../../styles/index.module.css';
import withAuth, {getAuth} from '../../components/withAuth'

function HomePage() {
    const [code, setCode] = useState('');
    const [error, setError] = useState('');
    const [isAuth, setAuth] = useState<boolean>(false);
    const router = useRouter();

    useEffect(() => {
        setAuth(getAuth);
    }, []);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        // Проверка кода
        if (!/^\d{5}$/.test(code)) {
            setError('Код должен состоять из 5 цифр');
            return;
        }

        // Перенаправляем на страницу пользователя
        router.push(`/admin/user/${code}`);
    };

    const navigateToUsersTable = () => {
        router.push('/admin/users');
    };

    return (
        <div className={styles.pageContainer}>
            <Head>
                <title>Поиск пользователя</title>
                <meta name="description" content="Поиск пользователя по коду" />
                <link rel="icon" href="/favicon.png" />
            </Head>
            { isAuth ?
            <main className={styles.main}>
                <div className={styles.card}>
                    <h1 className={styles.title}>Введите код клиента</h1>

                    {error && (
                        <div className={styles.errorMessage}>
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className={styles.form}>
                        <div className={styles.inputGroup}>
                            <label htmlFor="code" className={styles.inputLabel}>
                                5-значный код клиента
                            </label>
                            <input
                                type="text"
                                id="code"
                                value={code}
                                onChange={(e) => {
                                    setCode(e.target.value.replace(/\D/g, '').slice(0, 5));
                                    setError('');
                                }}
                                className={styles.inputField}
                                maxLength={5}
                                placeholder="12345"
                                required
                            />
                        </div>

                        <div className={styles.buttonGroup}>
                            <button
                                type="submit"
                                className={styles.submitButton}
                            >
                                Продолжить
                            </button>
                            <button
                                type="button"
                                onClick={navigateToUsersTable}
                                className={styles.usersTableButton}
                            >
                                Список пользователей
                            </button>
                        </div>
                    </form>
                </div>
            </main>
            : <div/>
            }
        </div>
    );
}


export default withAuth(HomePage);