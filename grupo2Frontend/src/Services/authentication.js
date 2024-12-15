import httpClient from "../http-common";

export const auth = async (to, from, next) => {
  try {
    const response = await httpClient.post("/auth/verify", {
      withCredentials: true,
    });

    if (response.status === 200) {
      const userRole = response.data.role;

      if (to.meta.roles && !to.meta.roles.includes(userRole)) {
        console.log("usuario no autorizado, redirigiendo");
        next({name: "login"});
      }
      console.log("Usuario autenticado");
      next();
    } else {
      console.log("No autenticado");
      next({name: "login"});
    }
  } catch (error) {
    console.log("Error de autenticación:", error);
    next({name: "login"});
  }
};

export const verifyToken = async () => {
  try {
    const response = await httpClient.post("/auth/verify", {
      withCredentials: true,
    });
    if (response.status === 200) {
      console.log("Usuario autenticado");
      const userRole = response.data.role;
      return userRole;
    } else {
      return false;
    }
  } catch (error) {
    return false;
  }
};