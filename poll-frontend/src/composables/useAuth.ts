import { reactive, readonly } from "vue";
import { AuthApi, type UserDto, type LoginRequest, type RegisterRequest } from "../api/auth";

// Simple in-memory auth state for session-backed auth
type AuthState = {
    user: UserDto | null;
    loading: boolean;
    error: string | null;
};

const state = reactive<AuthState>({
    user: null,
    loading: false,
    error: null,
});

// Load current session user from backend
async function refreshMe() {
    state.loading = true; state.error = null;
    try {
        const me = await AuthApi.me();
        state.user = me;
    } catch (e: any) {
        state.user = null; // 401 means no active session
    } finally {
        state.loading = false;
    }
}

async function login(payload: LoginRequest) {
    state.loading = true; state.error = null;
    try {
        const u = await AuthApi.login(payload);
        state.user = u; // cookie stored by browser due to credentials: "include"
    } catch (e: any) {
        state.error = e.message || "Login failed";
        state.user = null;
        throw e;
    } finally {
        state.loading = false;
    }
}

async function register(payload: RegisterRequest) {
    state.loading = true; state.error = null;
    try {
        const u = await AuthApi.register(payload);
        state.user = u; // optional: auto-login semantics; can also keep null and force login
    } catch (e: any) {
        state.error = e.message || "Register failed";
        throw e;
    } finally {
        state.loading = false;
    }
}

async function logout() {
    state.loading = true; state.error = null;
    try {
        await AuthApi.logout();
        state.user = null;
    } catch (e: any) {
        state.error = e.message || "Logout failed";
        throw e;
    } finally {
        state.loading = false;
    }
}

export function useAuth() {
    return {
        state: readonly(state),
        refreshMe,
        login,
        register,
        logout,
    };
}
