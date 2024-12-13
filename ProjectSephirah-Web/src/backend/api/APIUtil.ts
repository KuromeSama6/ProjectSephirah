import axios, { AxiosProgressEvent, AxiosRequestConfig, HttpStatusCode } from "axios";
import { APIResponse } from "./APIResponse";
import { SephirahAPI } from "./SephirahAPI.ts";
import { useSingletonsStore } from "../../stores/singletonsStore.ts";

export namespace APIUtil {
    export async function SendRequest<T = any>(method: string, path: string, data: object = {}, params: object = {}): Promise<APIResponse<T>> {
        try {
            const config: AxiosRequestConfig = {
                method: method,
                url: new URL(path, SephirahAPI.BASE_URL).href,
                headers: {
                    "Content-Type": "application/json",
                },
            };
            if (method === "GET") config.params = data;
            else config.data = data;
            if (params) config.params = params;

            // await new Promise((resolve) => setTimeout(resolve, 1000));

            const res = await axios(config);

            return {
                status: res.status,
                code: res.data.code,
                message: res.data.message,
                data: res.data.data,
                success: true,
                processed: true,
            };
        } catch (err: any) {
            if (err.response) {
                // useSingletonsStore().toastSingleton?.add({
                //     severity: "error",
                //     summary: `Error ${err.response.status} (${err.response.data.code})`,
                //     detail: err.response.data.message,
                //     life: 5000,
                // });

                // login expired
                return {
                    status: err.response.status,
                    code: err.response.data.code,
                    message: err.response.data.message,
                    data: err.response.data.data,
                    success: false,
                    processed: true,
                };
            } else {
                // useSingletonsStore().toastSingleton?.add({
                //     severity: "error",
                //     summary: `Connection error`,
                //     detail: "There was an error connecting to the server. Please check your internet connection and try again.",
                //     life: 5000,
                // });
                return {
                    status: HttpStatusCode.RequestTimeout,
                    code: -1,
                    message: "An error occurred while sending the request.",
                    success: false,
                    processed: false,
                };
            }
        }
    }

}
