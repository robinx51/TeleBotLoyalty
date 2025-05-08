import Link from 'next/link';
import { ReactNode } from 'react';

type LayoutProps = {
    children: ReactNode;
};

export default function Layout({ children }: LayoutProps) {
    return (
        <div>
            <nav className="bg-blue-600 text-white p-4">
                <div className="container mx-auto flex justify-between items-center">
                    <Link href="/admin" className="text-xl font-bold">Система лояльности</Link>
                    <div className="flex space-x-4">
                        <Link href="../../src/pages/admin/login.tsx" className="hover:underline">Авторизация</Link>
                        <Link href="../../src/pages/admin/index.tsx" className="hover:underline">Система лояльности</Link>
                        <Link href="../../src/pages/admin/users.tsx" className="hover:underline">Пользователи</Link>
                        <Link href="../../src/pages/admin/user/[code].tsx" className="hover:underline">Пользователь</Link>
                    </div>
                </div>
            </nav>
            <main>{children}</main>
        </div>
    );
}