import httpClient from "../http-common";

export const getPedidosByRepartidorId = async (idRepartidor) => {
    try {
        const response = await httpClient.get(`/pedido/repartidor/${idRepartidor}`);
        return response.data;
    } catch (error) {
        console.error("Error en la respuesta del servidor:", error.response.data);
        throw error;
    }
}

export const getOrdenByPedidoId = async (idPedido) => {
    try {
        const response = await httpClient.get(`/orden/pedido/${idPedido}`);
        return response.data;
    } catch (error) {
        console.error("Error en la respuesta del servidor:", error.response.data);
        throw error;
    }
}

export const getZonaById = async (idZona) => {
    try {
        const response = await httpClient.get(`pedido/zona/${idZona}`);
        return response.data;
    } catch (error) {
        console.error("Error en la respuesta del servidor:", error.response.data);
        throw error;
    }
}