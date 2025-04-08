import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import axios from 'axios';

interface UserData {
    code: number;
    telegramId: number;
    username: string;
    name: string;
    phoneNumber: string;
    cashback: number;
    action: 'earn' | 'spend';
    purchaseAmount: number;
    cashbackPercent: number;
}

export default function UserPage() {
    const router = useRouter();
    const { code } = router.query;
    const [userData, setUserData] = useState<UserData | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    // Загрузка данных пользователя
    useEffect(() => {
        if (!code) return;

        const fetchUserData = async () => {
            try {
                setLoading(true);
                const response = await axios.get(`/api/bot/users/getByCode?code=${code}`);
                const data = response.data;
                setUserData({
                    code: data.code,
                    telegramId: data.telegramId,
                    username: data.username || `ID: ${data.telegramId}`,
                    name: data.name || '',
                    phoneNumber: data.phoneNumber || '',
                    cashback: data.cashback || 0,
                    action: 'earn',
                    purchaseAmount: 0,
                    cashbackPercent: 0
                });
            } catch (err) {
                setError('Пользователь не найден');
                console.error('Error fetching user:', err);
            } finally {
                setLoading(false);
            }
        };

        fetchUserData();
    }, [code]);

    // Обработчик изменения телефонного номера с маской
    const handlePhoneChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (!userData) return;

        const input = e.target.value.replace(/\D/g, '');
        let formattedInput = '';

        if (input.length > 0) {
            formattedInput = `+7 (${input.substring(1, 4)}`;
            if (input.length > 4) formattedInput += `) ${input.substring(4, 7)}`;
            if (input.length > 7) formattedInput += `-${input.substring(7, 9)}`;
            if (input.length > 9) formattedInput += `-${input.substring(9, 11)}`;
        }

        setUserData({
            ...userData,
            phoneNumber: formattedInput
        });
    };

    // Обработчик изменения других полей
    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        if (!userData) return;

        const { name, value } = e.target;
        setUserData({
            ...userData,
            [name]: name === 'purchaseAmount' || name === 'cashbackPercent'
                ? parseFloat(value)
                : value
        });
    };

    // Отправка данных на сервер
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!userData) return;

        // Проверка заполнения всех обязательных полей
        if (!userData.name.trim()) {
            setError('Введите ФИ клиента');
            return;
        }

        if (!userData.phoneNumber || userData.phoneNumber.replace(/\D/g, '').length !== 11) {
            setError('Введите корректный номер телефона');
            return;
        }

        if (userData.purchaseAmount <= 0) {
            setError('Сумма покупки должна быть больше 0');
            return;
        }

        if (userData.cashbackPercent <= 0 || userData.cashbackPercent > 50) {
            setError('Процент кэшбека должен быть от 0 до 50');
            return;
        }

        try {
            setLoading(true);
            setError('');

            await axios.post('/api/bot/users/update', {
                code: userData.code,
                telegramId: userData.telegramId,
                name: userData.name,
                phoneNumber: userData.phoneNumber,
                cashback: userData.cashback,
                action: userData.action,
                purchaseAmount: userData.purchaseAmount,
                cashbackPercent: userData.cashbackPercent
            });

            setSuccess('Данные успешно сохранены!');
            setTimeout(() => {
                setSuccess('');
            }, 3000);
        } catch (err) {
            setError('Ошибка при сохранении данных');
            console.error('Error saving data:', err);
        } finally {
            setLoading(false);
        }
    };

    if (loading && !userData) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <div className="text-xl">Загрузка...</div>
            </div>
        );
    }

    if (!userData) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <div className="text-xl text-red-500">{error || 'Пользователь не найден'}</div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 py-8">
            <div className="max-w-2xl mx-auto bg-white p-6 rounded-lg shadow-md">
                <h1 className="text-2xl font-bold mb-6">Редактирование данных клиента</h1>

                {error && (
                    <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
                        {error}
                    </div>
                )}

                {success && (
                    <div className="mb-4 p-3 bg-green-100 border border-green-400 text-green-700 rounded">
                        {success}
                    </div>
                )}

                <form onSubmit={handleSubmit}>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                        {/* Неизменяемые данные */}
                        <div className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-500 mb-1">Код клиента</label>
                                <div className="p-2 border rounded bg-gray-50">{userData.code}</div>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-500 mb-1">Никнейм/ID</label>
                                <div className="p-2 border rounded bg-gray-50">{userData.username}</div>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-500 mb-1">Текущий кэшбэк</label>
                                <div className="p-2 border rounded bg-gray-50">
                                    {userData.cashback.toFixed(1)} баллов
                                </div>
                            </div>
                        </div>

                        {/* Изменяемые данные */}
                        <div className="space-y-4">
                            <div>
                                <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-1">
                                    Фамилия и имя*
                                </label>
                                <input
                                    type="text"
                                    id="name"
                                    name="name"
                                    value={userData.name}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    required
                                />
                            </div>

                            <div>
                                <label htmlFor="phoneNumber" className="block text-sm font-medium text-gray-700 mb-1">
                                    Номер телефона*
                                </label>
                                <input
                                    type="tel"
                                    id="phoneNumber"
                                    name="phoneNumber"
                                    value={userData.phoneNumber}
                                    onChange={handlePhoneChange}
                                    className="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    placeholder="+7 (999) 123-45-67"
                                    required
                                />
                            </div>

                            <div>
                                <label htmlFor="action" className="block text-sm font-medium text-gray-700 mb-1">
                                    Действие*
                                </label>
                                <select
                                    id="action"
                                    name="action"
                                    value={userData.action}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    required
                                >
                                    <option value="earn">Накопить</option>
                                    <option value="spend">Списать</option>
                                </select>
                            </div>

                            <div>
                                <label htmlFor="purchaseAmount" className="block text-sm font-medium text-gray-700 mb-1">
                                    Сумма покупки*
                                </label>
                                <input
                                    type="number"
                                    id="purchaseAmount"
                                    name="purchaseAmount"
                                    value={userData.purchaseAmount || ''}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    min="0"
                                    max="9999999.99"
                                    step="0.01"
                                    placeholder="0.00"
                                    required
                                />
                            </div>

                            <div>
                                <label htmlFor="cashbackPercent" className="block text-sm font-medium text-gray-700 mb-1">
                                    % кэшбека*
                                </label>
                                <input
                                    type="number"
                                    id="cashbackPercent"
                                    name="cashbackPercent"
                                    value={userData.cashbackPercent || ''}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    min="0"
                                    max="100"
                                    step="0.1"
                                    placeholder="0.0"
                                    required
                                />
                            </div>
                        </div>
                    </div>

                    <div className="flex justify-end space-x-4">
                        <button
                            type="button"
                            onClick={() => router.push('/')}
                            className="px-6 py-2 border border-gray-300 rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                            Назад
                        </button>
                        <button
                            type="submit"
                            disabled={loading}
                            className="px-6 py-2 border border-transparent rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50"
                        >
                            {loading ? 'Сохранение...' : 'Сохранить'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}