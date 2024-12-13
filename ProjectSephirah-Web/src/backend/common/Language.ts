import { ValueOf } from "../util/TypeExtensions.ts";

export const SupportedLanguages = ["cn", "en", "jp"] as const;
export type SupportedLanguage = (typeof SupportedLanguages)[number];