import httpClient from "../http-common";

export const getCategorias = async () => {
    try {
        const response = await httpClient.get("/categoria");
        return response.data;
    } catch (error) {
        console.error("Error en la respuesta del servidor:", error.response.data);
        throw error;
    }
}