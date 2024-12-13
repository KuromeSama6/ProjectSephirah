import Fuse from "fuse.js";
import { tify, sify } from "chinese-conv";

export namespace FuzzySearchUtil {
    export function NormalizeString(str: string): string {
        str = sify(str);

        return str;
    }
}