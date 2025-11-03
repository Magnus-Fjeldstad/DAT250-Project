// src/api/http.ts
// Production mode (served from Spring Boot @ http://localhost:8080)
// Use relative paths so cookies and session work automatically.

async function request(path: string, options: RequestInit = {}) {
    const resp = await fetch(path, {
        credentials: "include", // still needed, but now same-origin
        headers: {
            "Content-Type": "application/json",
        },
        ...options,
    });

    const raw = await resp.text();
    const body = raw ? (() => { try { return JSON.parse(raw); } catch { return raw; } })() : null;

    if (!resp.ok && resp.status !== 204) {
        throw new Error(`HTTP ${resp.status}: ${typeof body === "string" ? body : JSON.stringify(body)}`);
    }
    return body as any;
}

export const http = {
    get:  <T>(path: string): Promise<T> => request(path, { method: "GET" }),
    post: <T>(path: string, body: any): Promise<T> => request(path, { method: "POST", body: JSON.stringify(body) }),
    put:  <T>(path: string, body: any): Promise<T> => request(path, { method: "PUT", body: JSON.stringify(body) }),
    delete:      (path: string): Promise<void> => request(path, { method: "DELETE" }),
};
