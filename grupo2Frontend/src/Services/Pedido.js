import httpClient from "../http-common";

export async function getRepartidoresPorZona(comuna) {
  if (!comuna) {
    console.error("La comuna es requerida para buscar repartidores.");
    return null;
  }

  try {
    const response = await httpClient.get(`/pedido/repartidoresEnZona`, {
      params: { nombreComuna: comuna },
    });
    return response.data; 
  } catch (error) {
    console.error("Error al obtener los repartidores:", error);
    return null; 
  }
}

export const getPedidosSinAsignar = async () => {
  try {
    const response = await httpClient.get("/pedido/sinRepartidor");
    return response.data;
  } catch (error) {
    console.error("Error al obtener los pedidos sin asignar:", error);
    return null;
  }
}

export const updatePedido = async (id,pedido) => {
  try {
    const response = await httpClient.put(`/pedido/${id}`, pedido);
    return response.data;
  } catch (error) {
    console.error("Error al actualizar el pedido:", error);
    return null;
  }
}