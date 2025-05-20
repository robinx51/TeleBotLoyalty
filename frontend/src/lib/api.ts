import axios from 'axios';
import { User, UpdateUserRequest } from './types';

// Bot
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

// Auth
export const checkLogin = async (username: string, password: string): Promise<boolean> => {
    try {
        const response = await axios.post('/api/auth/login', { username, password }, {
            withCredentials: true,
            headers: {
                'Content-Type': 'application/json',
                'SameSite': 'None'
            }
        });
        return response.status === 200;
    } catch (error) {
        return false;
    }
};

export const postLogout = async (): Promise<void> => {
    try {
        await axios.post('/api/auth/logout', {}, { withCredentials: true });
    } catch (err) {
        console.error('Error logout: ' + err);
    }
}

export async function getAuth () : Promise<boolean> {
    try {
        const response = await axios.get('/api/auth/validate', {
            withCredentials: true,
        });
        return response.status === 200;
    } catch (err) {
        return false;
    }
}