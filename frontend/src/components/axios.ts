import axios from 'axios';
import {logout} from "../lib/auth";

const instance = axios.create({
    withCredentials: true, // Всегда отправляем куки
});

instance.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            logout(); // Вызываем logout при 401 ошибке
        }
        return Promise.reject(error);
    }
);

export default instance;