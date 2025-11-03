// src/api/votes.ts
// Vote API. Guests can read, but only authenticated can POST.
import { http } from "./http";

export interface VoteDto {
    id?: number;
    userId?: number;              // ignored by backend on POST
    optionId: number;
    pollId?: number;
    value: -1 | 0 | 1;
    publishedAt?: string;
}

export const VoteApi = {
    vote(v: VoteDto): Promise<VoteDto | undefined> { return http.post("/votes", v); }, // may return 204 -> undefined
    getByPoll(pollId: number): Promise<VoteDto[]>  { return http.get(`/polls/${pollId}/votes`); },
    getAll(): Promise<VoteDto[]>                   { return http.get("/votes"); },
};
