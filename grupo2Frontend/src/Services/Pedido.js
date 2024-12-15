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

export async function getPedidoById(idPedido) {
  if (!idPedido) {
    console.error("El id del repartidor es requerido para buscar pedidos.");
    return null;
  }

  try {
    const response = await httpClient.get(`/pedido/${idPedido}`);
    return response.data;
  } catch (error) {
    console.error("Error al obtener los pedidos del repartidor:", error);
    return null;
  }
}

export async function updatePedido(idPedido, pedido) {
  if (!idPedido) {
    console.error("El id del pedido es requerido para actualizar el pedido.");
    return null;
  }

  try {
    const response = await httpClient.put(`/pedido/${idPedido}`, pedido);
    return response.data;
  } catch (error) {
    console.error("Error al actualizar el pedido:", error);
    return null;
  }
}