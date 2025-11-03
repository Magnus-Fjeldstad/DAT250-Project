// src/composables/useAuth.ts
// Central reactive auth state using session cookies.
import { reactive, readonly } from "vue";
import { AuthApi, type UserDto, type LoginRequest, type RegisterRequest } from "../api/auth";

type AuthState = { user: UserDto | null; loading: boolean; error: string | null; };
const state = reactive<AuthState>({ user: null, loading: false, error: null });

async function refreshMe() {
    state.loading = true; state.error = null;
    try { state.user = await AuthApi.me(); } catch { state.user = null; } finally { state.loading = false; }
}

async function login(payload: LoginRequest) {
    state.loading = true; state.error = null;
    try { state.user = await AuthApi.login(payload); } catch (e: any) { state.error = e.message || "Login failed"; throw e; }
    finally { state.loading = false; }
}

async function register(payload: RegisterRequest) {
    state.loading = true; state.error = null;
    try { state.user = await AuthApi.register(payload); } catch (e: any) { state.error = e.message || "Register failed"; throw e; }
    finally { state.loading = false; }
}

async function logout() {
    state.loading = true; state.error = null;
    try { await AuthApi.logout(); state.user = null; } catch (e: any) { state.error = e.message || "Logout failed"; throw e; }
    finally { state.loading = false; }
}

export function useAuth() {
    return {
        state: readonly(state),
        refreshMe,
        login,
        register,
        logout,
        // Helpers to check role and ownership
        hasRole: (role: "ADMIN" | "USER") => state.user?.role === role,
        isAuthenticated: () => !!state.user,
        isOwner: (creatorId?: number | null) => !!state.user && !!creatorId && state.user.id === creatorId,
    };
}
