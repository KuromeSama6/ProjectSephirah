import { HttpStatusCode } from "axios";

export interface APIResponse<T = any> {
    status: HttpStatusCode;
    code: number;
    message: string;
    data?: T;
    success: boolean;
    processed: boolean;
}

export interface AuthenticationKeyPair {
    id: string;
    publicKey: string;
    expire: Date;
}