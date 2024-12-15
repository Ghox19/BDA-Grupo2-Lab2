import httpClient from "../http-common";

export async function obtenerRepartidoresPorZona(comuna) {
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
