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
                    <Link href="/" className="text-xl font-bold">Система лояльности</Link>
                    <div className="flex space-x-4">
                        <Link href="/src/pages/users" className="hover:underline">Пользователи</Link>
                        <Link href="/src/pages/index" className="hover:underline">Система лояльности</Link>
                        <Link href="/src/pages/user/[code]" className="hover:underline">Пользователь</Link>
                    </div>
                </div>
            </nav>
            <main>{children}</main>
        </div>
    );
}