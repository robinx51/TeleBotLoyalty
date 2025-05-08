import { useEffect } from 'react';
import { useRouter } from 'next/router';
import { checkAuth } from "../lib/api";

export default function withAuth(Component) {
    return (props) => {
        const router = useRouter();

        useEffect(() => {
            const handleAuth = async () => {
                const auth = localStorage.getItem('SmartStoreLoyaltyAuth');
                const basePage = 'admin';
                if (!auth) {
                    router.push(`/${basePage}/login`);
                    return;
                }

                try {
                    const response = await checkAuth(auth);

                    if (!response) {
                        router.push(`/${basePage}/login`);
                    }
                } catch (error) {
                    router.push(`/${basePage}/login`);
                }
            };

            handleAuth();
        }, []);

        return <Component {...props} />;
    };
}

export function getAuth():boolean {
    const auth = localStorage.getItem('SmartStoreLoyaltyAuth');
    return !!auth;
}