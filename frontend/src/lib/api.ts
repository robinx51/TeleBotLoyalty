import axios from 'axios';
import { User, UpdateUserRequest } from './types';

export async function getUsers(): Promise<User[] | null> {
    try {
        const response = await axios.get(`/api/bot/users/getAll`);
        return response.data.map((item: any) => ({
            code: item.code,
            telegramId: item.telegramId,
            username: item.username,
            name: item.name || '',
            phoneNumber: item.phoneNumber || '',
            cashback: item.cashback || 0
        }));
    } catch (error) {
        console.error('Error fetching users');
        throw error;
    }
}

export async function getUserByCode(code: string | string[]): Promise<User> {
    try {
        const response = await axios.get(`/api/bot/users/getByCode?code=${code}`);
        return response.data;
    } catch (err) {
        console.error('Error fetching user');
        throw err;
    }
}

export async function updateUser(data: UpdateUserRequest): Promise<boolean> {
    try {
        return await axios.post(`/api/bot/users/update`, data)
    } catch (err) {
        console.error('Error updating user');
        throw err;
    }

}

export async function checkAuth(data: string): Promise<boolean> {
    try {
        return await axios.get('/api/admin/check-auth', {
            headers: {
                'Authorization': 'Basic ' + data
            }
        });
    } catch (err) {
        console.error('Error checking auth');
        throw err;
    }
}