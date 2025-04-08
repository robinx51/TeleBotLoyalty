import { useState } from 'react';
import { useRouter } from 'next/router';

export default function HomePage() {
    const [code, setCode] = useState('');
    const [error, setError] = useState('');
    const router = useRouter();

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        // Проверка кода
        if (!/^\d{5}$/.test(code)) {
            setError('Код должен состоять из 5 цифр');
            return;
        }

        // Перенаправляем на страницу пользователя
        router.push(`/user/${code}`);
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50">
            <div className="max-w-md w-full bg-white p-8 rounded-lg shadow-md">
                <h1 className="text-2xl font-bold text-center mb-6">Введите код клиента</h1>

                {error && (
                    <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        <label htmlFor="code" className="block text-sm font-medium text-gray-700 mb-1">
                            5-значный код клиента
                        </label>
                        <input
                            type="text"
                            id="code"
                            value={code}
                            onChange={(e) => {
                                setCode(e.target.value.replace(/\D/g, '').slice(0, 6));
                                setError('');
                            }}
                            className="w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                            maxLength={5}
                            placeholder="12345"
                            required
                        />
                    </div>

                    <button
                        type="submit"
                        className="w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                    >
                        Продолжить
                    </button>
                </form>
            </div>
        </div>
    );
}