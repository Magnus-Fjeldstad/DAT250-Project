// src/api/users.ts
import { http } from "./http";

export interface User {
    id?: number;
    username: string;
}

export const UserApi = {
    getAll(): Promise<User[]> {
        return http.get("/users");
    },
    getById(id: number): Promise<User> {
        return http.get(`/users/${id}`);
    },
    create(user: User): Promise<User> {
        return http.post("/users", user);
    },
    update(id: number, user: User): Promise<User> {
        return http.put(`/users/${id}`, user);
    },
    delete(id: number): Promise<void> {
        return http.delete(`/users/${id}`);
    },
};
