import axios, {AxiosResponse} from 'axios';
import { User, TransactionFormData } from './types';

export async function getUsers(): Promise<User[] | null> {
    try {
        const response: AxiosResponse = await axios.get(`/api/bot/users/getAll`);
        return response.data.map((item: any) => ({
            code: item.code,
            telegramId: item.telegramId,
            username: item.username,
            name: item.name || '',
            phoneNumber: item.phoneNumber || '',
            cashback: item.cashback || 0
        }));
    } catch (error) {
        console.error('Error fetching users:', error);
        return null;
    }
}

export async function getUserByCode(code: string | string[]): Promise<User | null> {
    try {
        const response = await axios.get(`/api/bot/users/getByCode?code=${code}`);
        return response.data;
    } catch (error) {
        if (axios.isAxiosError(error) && error.response?.status === 404) {
            return null;
        }
        throw error;
    }
}

export async function updateUser(user: User): Promise<boolean> {
    try {
        await axios.post(`/api/bot/users/update`, user);
        return true;
    } catch (error) {
        console.error(`Error updating user ${user.code}:`, error);
        return false;
    }
}

export async function processTransaction(data: TransactionFormData): Promise<boolean> {
    try {
        await axios.post(`/api/bot/transactions`, data);
        return true;
    } catch (error) {
        console.error('Error processing transaction:', error);
        return false;
    }
}