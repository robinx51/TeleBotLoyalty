import React, {useEffect, useState} from 'react';
import { useRouter } from 'next/router';
import styles from "../styles/index.module.css";
import {login, logout} from "../lib/auth";
import Head from "next/head";

export default function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const router = useRouter();

    useEffect(() => {
        logout();
    }, []);

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');

        try {
            const response = await login(username, password);
            if (response) {
                router.push('/');
            } else {
                setError('Неверные учетные данные');
            }
        } catch (error: any) {
            setError(error.message);
            console.error('Login failed:', error);
        }
    };

    return (
        <div className={styles.pageContainer}>
            <Head>
                <title>Авторизация</title>
                <meta name="description" content="Авторизация"/>
                <link rel="icon" href="/favicon.png"/>
            </Head>
            <main className={styles.main}>
                <div className={styles.card}>
                    {error && <div className={styles.errorMessage}>{error}</div>}
                    <h1 className={styles.title}>Авторизация</h1>
                    <form onSubmit={handleLogin} className={styles.form}>
                        <input
                            type="text"
                            placeholder="Логин"
                            value={username}
                            className={styles.inputField}
                            onChange={(e) => setUsername(e.target.value)}
                        />
                        <input
                            type="password"
                            placeholder="Пароль"
                            value={password}
                            className={styles.inputField}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                        <button
                            type="submit"
                            className={styles.submitButton}
                        >
                            Войти
                        </button>
                    </form>
                </div>
            </main>
        </div>
);
}