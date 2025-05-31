import {deleteCookie, setCookie} from 'cookies-next';
import {checkLogin, getAuth, postLogout} from "./api";

const cookieOptions = {
    maxAge: 60 * 60 * 24, // 1 день
    path: '/',
    sameSite: 'strict' as const,
};

export const login = async (username: string, password: string): Promise<boolean> => {
    try {
        const response = await checkLogin(username,  password);

        if (response) {
            setCookie('SmartStoreIsAuth', 'true', cookieOptions);
            return true;
        }
        clearCookies();
        return false;
    } catch (error: any) {
        console.error('Login error:', error);
        clearCookies();
        throw new Error(
            error.response?.data?.message ||
            'Ошибка соединения с сервером'
        );
    }
};

export const logout = async (): Promise<void> => {
    try {
        await postLogout();
    } finally {
        clearCookies();
    }
};

export const checkAuth = async (): Promise<boolean> => {
    try {
        return await getAuth();
    } catch {
        clearCookies();
        return false;
    }
};

export const clearCookies = () => {
    deleteCookie('SmartStoreIsAuth');
    deleteCookie('authTokenSmartStore');
}
