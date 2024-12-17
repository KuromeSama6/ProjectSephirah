/**
 * A Provider represents an external web service that provides data related to the application. This may be a Manga provider
 * that serves manga pictures itself, or a tracker service that tracks users' reading progress.
 */
export interface Provider {
    readonly id: string;

    CheckAvailability(): Promise<boolean>;

    auth?: AuthenticationProvider;
}

export interface AuthenticationProvider {
    GetCredentialsObject(): any;

    Authenticate(credential: any): Promise<AuthenticationResponse>;

    Deauthenticate(): Promise<void>;

    ValidateToken(): Promise<ProviderTokenValidationStatus>;
}

export enum ProviderStatus {
    Unknown,
    Available,
    Unavailable,
}

export enum ProviderTokenValidationStatus {
    VALID,
    INVALID,
    REQUEST_FAILED,
}

export interface AuthenticationResponse {
    success: boolean;
    token?: string;
    message?: string;
}