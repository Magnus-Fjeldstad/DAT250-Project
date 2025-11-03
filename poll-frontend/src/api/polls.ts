// src/api/polls.ts
// Poll API aligned with your backend DTO.
import { http } from "./http";

export interface VoteOptionDto {
    id?: number;
    caption: string;
    presentationOrder: number;
}

export interface PollDto {
    id?: number;
    question: string;
    publishedAt?: string | null;
    validUntil?: string | null;
    creatorId?: number | null;
    options: VoteOptionDto[];
}

export const PollApi = {
    getAll(): Promise<PollDto[]>        { return http.get("/polls"); },
    getById(id: number): Promise<PollDto> { return http.get(`/polls/${id}`); },
    create(dto: PollDto): Promise<PollDto> { return http.post("/polls", dto); }, // creator taken from session
    update(id: number, dto: PollDto): Promise<PollDto> { return http.put(`/polls/${id}`, dto); },
    delete(id: number): Promise<void>   { return http.delete(`/polls/${id}`); },
    getResults(id: number): Promise<any> { return http.get(`/polls/${id}/results`); },
};
