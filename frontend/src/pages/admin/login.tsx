import React, { useState } from 'react';
import { useRouter } from 'next/router';
import { checkAuth } from '../../lib/api';
import styles from "../../styles/users.module.css";

export default function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const router = useRouter();

    const handleLogin = async (e) => {
        e.preventDefault();
        const data = btoa(`${username}:${password}`);
        const basePage = 'admin';
        try {
            const response = await checkAuth(data);
            if (response) {
                localStorage.setItem('SmartStoreLoyaltyAuth', data);
                router.push(`/${basePage}`);
            } else {
                alert('Invalid credentials');
            }
        } catch (error) {
            setError('Ошибка авторизации: сервер недоступен');
        }
    };

    return (
        <div>
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