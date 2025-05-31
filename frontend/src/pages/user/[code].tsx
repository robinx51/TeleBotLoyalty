import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { getUserByCode, updateUser } from '../../lib/api';
import { User, UpdateUserRequest } from '../../lib/types';
import styles from '../../styles/[code].module.css';
import Head from "next/head";
import { AxiosError } from "axios";
import withAuth, {getAuth} from '../../components/withAuth'

export function getFormattedPhone(inputStr: string ): string {
    const input = inputStr === null ?  "" : inputStr.replace(/\D/g, '');
    let formattedInput = '';

    if (input.length > 0) {
        formattedInput = `+7 (${input.substring(1, 4)}`;
        if (input.length > 4) formattedInput += `) ${input.substring(4, 7)}`;
        if (input.length > 7) formattedInput += `-${input.substring(7, 9)}`;
        if (input.length > 9) formattedInput += `-${input.substring(9, 11)}`;
    }
    return formattedInput;
}

function UserPage() {
    const router = useRouter();
    const { code } = router.query;
    const [user, setUser] = useState<User | null>(null);
    const [formData, setFormData] = useState({
        name: '',
        phoneNumber: '',
        action: 'earn' as 'earn' | 'spend',
        purchaseAmount: '',
        cashbackPercent: '',
        cashbackAmount: '',
        operationAmount: ''
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [isAuth, setAuth] = useState<boolean>(false);

    // Расчетные значения
    const currentCashback = user?.cashback || 0;
    const cashbackAfterOperation = formData.action === 'earn'
        ? currentCashback + parseInt(formData.cashbackAmount || '0')
        : currentCashback - parseInt(formData.operationAmount || '0');

    // Загрузка данных пользователя
    useEffect(() => {
        if (!code) return;

        const fetchUserData = async () => {
            setLoading(true);

            await getUserByCode(code)
                .then(function (userData) {
                    setUser(userData);
                    setFormData(prev => ({
                        ...prev,
                        name: userData.name || '',
                        phoneNumber: userData.phoneNumber ? getFormattedPhone(userData.phoneNumber) : ''
                    }));
                })
                .catch(function (error) {
                    if (error instanceof AxiosError && error.response?.status === 500)
                        setError('Соединение с сервером не установлено');
                    else
                        setError('Неизвестная ошибка');
                });
            setLoading(false);
        };

        setAuth(getAuth);
        fetchUserData();
    }, [code]);

    // Обработчики изменений
    const handlePhoneChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({
            ...formData,
            phoneNumber: getFormattedPhone(e.target.value)
        });
    };

    const handleActionChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const action = e.target.value as 'earn' | 'spend';
        setFormData({
            ...formData,
            action,
            purchaseAmount: '',
            cashbackPercent: '',
            cashbackAmount: '',
            operationAmount: ''
        });
    };

    const handlePurchaseAmountChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const purchaseAmount = e.target.value;
        const cashbackPercent = purchaseAmount && formData.cashbackAmount
            ? ((parseInt(formData.cashbackAmount) / parseInt(purchaseAmount)) * 100).toFixed(1)
            : '';

        setFormData({
            ...formData,
            purchaseAmount,
            cashbackPercent
        });
    };

    const handleCashbackPercentChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const cashbackPercent = e.target.value;
        const cashbackAmount = cashbackPercent && formData.purchaseAmount
            ? (parseInt(formData.purchaseAmount) * parseFloat(cashbackPercent) / 100).toFixed(0)
            : '';

        setFormData({
            ...formData,
            cashbackPercent,
            cashbackAmount
        });
    };

    const handleCashbackAmountChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const cashbackAmount = e.target.value;
        const cashbackPercent = cashbackAmount && formData.purchaseAmount
            ? ((parseInt(cashbackAmount) / parseInt(formData.purchaseAmount)) * 100).toFixed(1)
            : '';

        setFormData({
            ...formData,
            cashbackAmount,
            cashbackPercent
        });
    };

    const handleOperationAmountChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({
            ...formData,
            operationAmount: e.target.value
        });
    };

    const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({
            ...formData,
            name: e.target.value
        });
    };

    // Отправка данных на сервер
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!user) return;

        // Валидация
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
            const cashbackAmount = parseInt(formData.cashbackAmount || '0');
            if (cashbackAmount <= 0) {
                setError('Сумма кэшбэка должна быть больше 0');
                return;
            }
        } else {
            const operationAmount = parseInt(formData.operationAmount || '0');
            if (operationAmount <= 0) {
                setError('Количество списываемых баллов должно быть больше 0');
                return;
            }
            if (operationAmount > currentCashback) {
                setError('Недостаточно баллов для списания');
                return;
            }
        }

        try {
            setLoading(true);
            setError('');

            const requestData: UpdateUserRequest = {
                code: user.code,
                telegramId: user.telegramId,
                name: formData.name,
                phoneNumber: phoneDigits,
                cashback: cashbackAfterOperation,
                action: formData.action,
                operationAmount: formData.action === 'earn'
                    ? parseInt(formData.cashbackAmount || '0')
                    : parseInt(formData.operationAmount || '0')
            };

            await updateUser(requestData)
                .then(function (isSuccess) {
                    if (isSuccess){
                        setSuccess('Данные успешно сохранены!');
                        setTimeout(() => {
                            router.reload();
                            setSuccess('');
                        }, 3000);
                    } else setError('Ошибка сохранения данных');
                })
                .catch(function (error) {
                    if (error instanceof AxiosError && error.response?.status === 500)
                        setError('Соединение с сервером потеряно');
                    else
                        setError('Неизвестная ошибка');
                });
        } finally {
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
            { isAuth ?
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
                                        <div className={styles.infoLabel}>Никнейм</div>
                                        <div className={styles.infoValue}>{user.username || 'скрытый'}</div>
                                    </div>

                                    <div className={styles.infoItem}>
                                        <div className={styles.infoLabel}>Текущий кэшбэк</div>
                                        <div className={styles.infoValue}>{currentCashback.toFixed(0)} баллов</div>
                                    </div>

                                    <div className={styles.formGroup}>
                                        <label htmlFor="name" className={styles.formLabel}>
                                            Фамилия и имя*
                                        </label>
                                        {!user.name ? (
                                            <input
                                                type="text"
                                                id="name"
                                                name="name"
                                                value={formData.name}
                                                onChange={handleNameChange}
                                                className={styles.inputField}
                                                required
                                            />) : (
                                            <div className={styles.infoValue}>{user.name}</div>
                                            )}
                                    </div>

                                    <div className={styles.formGroup}>
                                        <label htmlFor="phoneNumber" className={styles.formLabel}>
                                            Номер телефона*
                                        </label>
                                        {!user.phoneNumber ? (
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
                                            ) : (
                                            <div className={styles.infoValue}>{formData.phoneNumber}</div>
                                            )}
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
                                            onChange={handleActionChange}
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
                                                    Сумма покупки
                                                </label>
                                                <input
                                                    type="number"
                                                    id="purchaseAmount"
                                                    name="purchaseAmount"
                                                    value={formData.purchaseAmount}
                                                    onChange={handlePurchaseAmountChange}
                                                    className={styles.inputField}
                                                    min="0"
                                                    step="1"
                                                    placeholder="0"
                                                />
                                            </div>

                                            <div className={styles.formGroup}>
                                                <label htmlFor="cashbackPercent" className={styles.formLabel}>
                                                    % кэшбека
                                                </label>
                                                <input
                                                    type="number"
                                                    id="cashbackPercent"
                                                    name="cashbackPercent"
                                                    value={formData.cashbackPercent}
                                                    onChange={handleCashbackPercentChange}
                                                    className={styles.inputField}
                                                    min="0"
                                                    step="0.1"
                                                    placeholder="0.0"
                                                />
                                            </div>

                                            <div className={styles.formGroup}>
                                                <label htmlFor="cashbackAmount" className={styles.formLabel}>
                                                    Начислить баллов*
                                                </label>
                                                <input
                                                    type="number"
                                                    id="cashbackAmount"
                                                    name="cashbackAmount"
                                                    value={formData.cashbackAmount}
                                                    onChange={handleCashbackAmountChange}
                                                    className={styles.inputField}
                                                    min="0"
                                                    step="1"
                                                    placeholder="0"
                                                    required
                                                />
                                            </div>

                                            <div className={styles.infoItem}>
                                                <div className={styles.infoLabel}>Будет на счету</div>
                                                <div className={styles.infoValue}>
                                                    {cashbackAfterOperation.toFixed(0)} баллов
                                                </div>
                                            </div>
                                        </>
                                    ) : (
                                        <>
                                            <div className={styles.formGroup}>
                                                <label htmlFor="operationAmount" className={styles.formLabel}>
                                                    Списать баллов*
                                                </label>
                                                <input
                                                    type="number"
                                                    id="operationAmount"
                                                    name="operationAmount"
                                                    value={formData.operationAmount}
                                                    onChange={handleOperationAmountChange}
                                                    className={styles.inputField}
                                                    min="0"
                                                    max={currentCashback}
                                                    step="1"
                                                    placeholder="0"
                                                    required
                                                />
                                            </div>

                                            <div className={styles.infoItem}>
                                                <div className={styles.infoLabel}>Останется баллов</div>
                                                <div className={styles.infoValue}>
                                                    {Math.max(0, cashbackAfterOperation).toFixed(0)}
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
            : <div/>
            }
        </div>
    );
}


export default withAuth(UserPage);