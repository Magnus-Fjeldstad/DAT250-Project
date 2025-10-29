import { http } from "./http";

// Backend user DTO we return from /auth endpoints
export interface UserDto {
    id: number;
    username: string;
    email: string;
    role: string;
}

export interface LoginRequest { username: string; password: string; }
export interface RegisterRequest { username: string; email: string; password: string; }

export const AuthApi = {
    register(data: RegisterRequest): Promise<UserDto> {
        return http.post("/auth/register", data);
    },
    login(data: LoginRequest): Promise<UserDto> {
        return http.post("/auth/login", data);
    },
    me(): Promise<UserDto> {
        return http.get("/auth/me");
    },
    logout(): Promise<{ message: string }> {
        return http.post("/auth/logout", {});
    }
};
