/**
 * A Provider represents an external web service that provides data related to the application. This may be a Manga provider
 * that serves manga pictures itself, or a tracker service that tracks users' reading progress.
 */
export interface Provider {
    readonly id: string;

    CheckAvailability(): Promise<boolean>;
}

export enum ProviderStatus {
    Unknown,
    Available,
    Unavailable,
}