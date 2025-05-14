export interface User {
    code: number;
    telegramId: number;
    username: string;
    name: string;
    phoneNumber: string;
    cashback: number;
}

export interface UpdateUserRequest {
    code: number;
    telegramId: number;
    name: string;
    phoneNumber: string;
    cashback: number;
    action: 'earn' | 'spend';
    operationAmount: number;
}

export interface LoginRequest {
    username: string;
    password: string;
}

export interface AuthResponse {
    authenticated: boolean;
    username?: string;
}