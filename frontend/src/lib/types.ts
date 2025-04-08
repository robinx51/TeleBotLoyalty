export interface User {
    code: number;
    telegramId: number;
    username: string;
    name: string;
    phoneNumber: string;
    cashback: number;
}

export interface TransactionFormData {
    code: string;
    name: string;
    phoneNumber: string;
    action: 'earn' | 'spend';
    amount: number;
    cashbackPercent: number;
}

export interface UpdateUserRequest {
    code: number;
    telegramId: number;
    name: string;
    phoneNumber: string;
    cashback: number;
    action: 'earn' | 'spend';
    purchaseAmount: number;
    cashbackPercent: number;
}