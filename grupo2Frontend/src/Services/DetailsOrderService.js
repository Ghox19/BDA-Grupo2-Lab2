import httpClient from "../http-common";

export const getDetailsOrderbyOrder = async (idOrden) => {
    try {
        const response = await httpClient.get(`/detalleOrden/orden/${idOrden}`);
        return response.data;
    } catch (error) {
        console.error("Error en la respuesta del servidor:", error.response.data);
        throw error;
    }
}

export const deleteDetailsOrder = async (id) => {
    try {
        const response = await httpClient.delete(`/detalleOrden/${id}`);
        return response.data;
    } catch (error) {
        console.error("Error en la respuesta del servidor:", error.response.data);
        throw error;
    }
}

export const createDetailsOrder = async (detailsOrder) => {
    try {
        const response = await httpClient.post("/detalleOrden", detailsOrder);
        return response.data;
    } catch (error) {
        console.error("Error en la respuesta del servidor:", error.response.data);
        throw error;
    }
}