export default function ({ store, redirect }) {
    if (!store.state.auth.isAuthenticated) {
        return redirect('/login');
    }
    
    const user = store.state.auth.user;

    if (!user || user.role !== 'ROLE_ADMIN') {
        return redirect('/dashboard'); 
    }
}