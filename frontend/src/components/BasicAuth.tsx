import initializeBasicAuth from 'nextjs-basic-auth'

const users = [
    { user: 'user1', password: 'toocool' },
    { user: 'admin', password: 'password' },
]
export const basicAuthCheck = initializeBasicAuth({
    users: users
})