import axios, { HttpStatusCode } from "axios";

export default {
    async CheckUrlAvailability(url: string): Promise<boolean> {
        try {
            const ret = await axios.get(url);
            return ret.status == HttpStatusCode.Ok;

        } catch (error: any) {
            return false;
        }
    },
};