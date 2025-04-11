import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { getUserByCode, updateUser } from '../../lib/api';
import { User, UpdateUserRequest } from '../../lib/types';
import styles from '../../styles/[code].module.css';
import Head from "next/head";
import { AxiosError } from "axios";

export function getFormattedPhone (e: React.ChangeEvent<HTMLInputElement>) : string {
    const input = e.target.value.replace(/\D/g, '');
    let formattedInput = '';

    if (input.length > 0) {
        formattedInput = `+7 (${input.substring(1, 4)}`;
        if (input.length > 4) formattedInput += `) ${input.substring(4, 7)}`;
        if (input.length > 7) formattedInput += `-${input.substring(7, 9)}`;
        if (input.length > 9) formattedInput += `-${input.substring(9, 11)}`;
    }
    return formattedInput;
}

export default function UserPage() {
    const router = useRouter();
    const { code } = router.query;
    const [user, setUser] = useState<User | null>(null);
    const [formData, setFormData] = useState<Omit<UpdateUserRequest, 'code' | 'telegramId' | 'cashback'>>({
        name: '',
        phoneNumber: '',
        action: 'earn',
        purchaseAmount: 0,
        cashbackPercent: 0,
        spendAmount: 0
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const cashbackToAdd = formData.action === 'earn'
        ? (formData.purchaseAmount * formData.cashbackPercent / 100.0).toFixed(1)
        : '0.0';

    const cashbackAfterOperation = formData.action === 'earn'
        ? (user?.cashback || 0) + parseFloat(cashbackToAdd)
        : (user?.cashback || 0) - (formData.spendAmount || 0);

    // Загрузка данных пользователя
    useEffect(() => {
        if (!code) return;

        const fetchUserData = async () => {
            setLoading(true);

            await getUserByCode(code)
                .then(function (userData) {
                    setUser(userData);
                    setFormData({
                        name: userData.name || '',
                        phoneNumber: userData.phoneNumber || '',
                        action: 'earn',
                        purchaseAmount: 0,
                        cashbackPercent: 0,
                        spendAmount: 0
                    });
                })
                .catch(function (error) {
                    if (error instanceof AxiosError && error.response.status == 500)
                        setError('Соединение с сервером не установлено');
                    else
                        setError('Неизвестная ошибка');
                });
            setLoading(false);
        };

        fetchUserData();
    }, [code]);

    const handlePhoneChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({
            ...formData,
            phoneNumber: getFormattedPhone(e)
        });
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: name === 'purchaseAmount' || name === 'cashbackPercent' || name === 'spendAmount'
                ? parseFloat(value) || 0
                : value
        });
    };

    // Отправка данных на сервер
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!user) return;

        if (!formData.name.trim()) {
            setError('Введите ФИ клиента');
            return;
        }

        const phoneDigits = formData.phoneNumber.replace(/\D/g, '');
        if (phoneDigits.length < 11) {
            setError('Введите корректный номер телефона (минимум 11 цифр)');
            return;
        }

        if (formData.action === 'earn') {
            if (formData.purchaseAmount <= 0) {
                setError('Сумма покупки должна быть больше 0');
                return;
            }
            if (formData.cashbackPercent <= 0 || formData.cashbackPercent > 50) {
                setError('Процент кэшбека должен быть от 0 до 50');
                return;
            }
        } else {
            if (formData.spendAmount <= 0) {
                setError('Количество списываемых баллов должно быть больше 0');
                return;
            }
            if (formData.spendAmount > user.cashback) {
                setError('Недостаточно баллов для списания');
                return;
            }
        }

        {
            setLoading(true);
            setError('');

            const requestData: UpdateUserRequest = {
                code: user.code,
                telegramId: user.telegramId,
                name: formData.name,
                phoneNumber: formData.phoneNumber,
                cashback: cashbackAfterOperation,
                action: formData.action,
                purchaseAmount: formData.action === 'earn' ? formData.purchaseAmount : 0,
                cashbackPercent: formData.action === 'earn' ? formData.cashbackPercent : 0,
                spendAmount: formData.action === 'spend' ? formData.spendAmount : 0
            };

            await updateUser(requestData)
                .then(function () {
                    setSuccess('Данные успешно сохранены!');
                    setTimeout(() => {
                        setSuccess('');
                        router.reload();
                    }, 3000);
                })
                .catch(function (err) {
                    setError('Ошибка при сохранении данных');
                    console.error('Error saving data:', err);
                });
            setLoading(false);
        }

    };

    if (loading && !user) {
        return <div className={styles.loading}>Загрузка...</div>;
    }

    if (!user) {
        return <div className={styles.errorMessage}>{error || 'Пользователь не найден'}</div>;
    }

    return (
        <div className={styles.pageContainer}>
            <Head>
                <title>Новая операция</title>
                <meta name="description" content="Новая операция" />
                <link rel="icon" href="/favicon.png" />
            </Head>

            <main className={styles.main}>
                <div className={styles.card}>
                    <h1 className={styles.title}>Новая операция</h1>

                    {error && <div className={styles.errorMessage}>{error}</div>}
                    {success && <div className={styles.successMessage}>{success}</div>}

                    <form onSubmit={handleSubmit} className={styles.form}>
                        <div className={styles.twoColumns}>
                            <div className={styles.leftColumn}>
                                <div className={styles.infoSection}>
                                    <div className={styles.infoItem}>
                                        <div className={styles.infoLabel}>Код клиента</div>
                                        <div className={styles.infoValue}>{user.code}</div>
                                    </div>

                                    <div className={styles.infoItem}>
                                        <div className={styles.infoLabel}>Никнейм/ID</div>
                                        <div className={styles.infoValue}>{user.username || `ID: ${user.telegramId}`}</div>
                                    </div>

                                    <div className={styles.infoItem}>
                                        <div className={styles.infoLabel}>Текущий кэшбэк</div>
                                        <div className={styles.infoValue}>{user.cashback.toFixed(1)} баллов</div>
                                    </div>

                                    <div className={styles.formGroup}>
                                        <label htmlFor="name" className={styles.formLabel}>
                                            Фамилия и имя*
                                        </label>
                                        <input
                                            type="text"
                                            id="name"
                                            name="name"
                                            value={formData.name}
                                            onChange={handleChange}
                                            className={styles.inputField}
                                            required
                                        />
                                    </div>

                                    <div className={styles.formGroup}>
                                        <label htmlFor="phoneNumber" className={styles.formLabel}>
                                            Номер телефона*
                                        </label>
                                        <input
                                            type="tel"
                                            id="phoneNumber"
                                            name="phoneNumber"
                                            value={formData.phoneNumber}
                                            onChange={handlePhoneChange}
                                            className={`${styles.inputField} ${styles.phoneInput}`}
                                            placeholder="+7 (999) 999-99-99"
                                            required
                                        />
                                    </div>
                                </div>
                            </div>

                            <div className={styles.rightColumn}>
                                <div className={styles.operationSection}>
                                    <div className={styles.formGroup}>
                                        <label htmlFor="action" className={styles.formLabel}>
                                            Действие*
                                        </label>
                                        <select
                                            id="action"
                                            name="action"
                                            value={formData.action}
                                            onChange={handleChange}
                                            className={styles.selectField}
                                            required
                                        >
                                            <option value="earn">Накопить</option>
                                            <option value="spend">Списать</option>
                                        </select>
                                    </div>

                                    {formData.action === 'earn' ? (
                                        <>
                                            <div className={styles.formGroup}>
                                                <label htmlFor="purchaseAmount" className={styles.formLabel}>
                                                    Сумма покупки*
                                                </label>
                                                <input
                                                    type="number"
                                                    id="purchaseAmount"
                                                    name="purchaseAmount"
                                                    value={formData.purchaseAmount || ''}
                                                    onChange={handleChange}
                                                    className={styles.inputField}
                                                    min="0"
                                                    max="9999999.99"
                                                    step="1"
                                                    placeholder="0.00"
                                                    required
                                                />
                                            </div>

                                            <div className={styles.formGroup}>
                                                <label htmlFor="cashbackPercent" className={styles.formLabel}>
                                                    % кэшбека*
                                                </label>
                                                <input
                                                    type="number"
                                                    id="cashbackPercent"
                                                    name="cashbackPercent"
                                                    value={formData.cashbackPercent || ''}
                                                    onChange={handleChange}
                                                    className={styles.inputField}
                                                    min="0"
                                                    max="50"
                                                    step="1"
                                                    placeholder="0.0"
                                                    required
                                                />
                                            </div>

                                            <div className={styles.infoItem}>
                                                <div className={styles.infoLabel}>Накопится баллов</div>
                                                <div className={styles.infoValue}>{cashbackToAdd}</div>
                                            </div>
                                        </>
                                    ) : (
                                        <>
                                            <div className={styles.formGroup}>
                                                <label htmlFor="spendAmount" className={styles.formLabel}>
                                                    Списать баллов*
                                                </label>
                                                <input
                                                    type="number"
                                                    id="spendAmount"
                                                    name="spendAmount"
                                                    value={formData.spendAmount || ''}
                                                    onChange={handleChange}
                                                    className={styles.inputField}
                                                    min="0"
                                                    max={user.cashback}
                                                    step="100"
                                                    placeholder="0.0"
                                                    required
                                                />
                                            </div>

                                            <div className={styles.infoItem}>
                                                <div className={styles.infoLabel}>Останется баллов</div>
                                                <div className={styles.infoValue}>
                                                    {Math.max(0, cashbackAfterOperation).toFixed(1)}
                                                </div>
                                            </div>
                                        </>
                                    )}
                                </div>
                            </div>
                        </div>

                        <div className={styles.buttons}>
                            <button
                                type="button"
                                onClick={() => router.push('/')}
                                className={styles.secondaryButton}
                            >
                                Назад
                            </button>
                            <button
                                type="submit"
                                disabled={loading}
                                className={styles.primaryButton}
                            >
                                {loading ? 'Сохранение...' : 'Сохранить'}
                            </button>
                        </div>
                    </form>
                </div>
            </main>
        </div>
    );
}