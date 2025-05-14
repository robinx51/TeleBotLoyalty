import { useEffect } from 'react';
import { getCookie } from 'cookies-next';
import { useRouter } from 'next/router';
import { checkAuth } from "../lib/auth";


export default function withAuth(Component) {
    return (props) => {
        const router = useRouter();

        useEffect(() => {
            const response = checkAuth();
            response.then(function (isAuth) {
                if (!isAuth) {
                    router.push('/admin/login');
                }
            })
        }, []);

        return <Component {...props} />;
    };
}

export function getAuth():boolean {
    const auth = getCookie('SmartStoreIsAuth');
    return !!auth;
}