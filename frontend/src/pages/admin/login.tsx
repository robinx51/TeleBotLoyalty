import React, {useEffect, useState} from 'react';
import { useRouter } from 'next/router';
import styles from "../../styles/users.module.css";
import {login, logout} from "../../lib/auth";
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
                router.push('/admin');
            } else {
                setError('Неверные учетные данные');
            }
        } catch (error: any) {
            setError(error.message);
            console.error('Login failed:', error);
        }
    };

    return (
        <div>
            <Head>
                <title>Авторизация</title>
                <meta name="description" content="Авторизация" />
                <link rel="icon" href="/favicon.png" />
            </Head>
            {error && <div className={styles.errorMessage}>{error}</div>}
            <h1>Авторизация</h1>
            <form onSubmit={handleLogin}>
                <input
                    type="text"
                    placeholder="Логин"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="Пароль"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <button type="submit">Войти</button>
            </form>
        </div>
    );
}