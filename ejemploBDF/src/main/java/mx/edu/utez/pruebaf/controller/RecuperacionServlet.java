package mx.edu.utez.pruebaf.controller;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.edu.utez.pruebaf.SimpleRandomStringGenerator;
import mx.edu.utez.pruebaf.dao.UserDao;
import mx.edu.utez.pruebaf.model.User;
import mx.edu.utez.pruebaf.utils.GmailSender;

import java.io.IOException;

@WebServlet(name="RecuperacionServlet",value="/recucontra")
public class RecuperacionServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Porque estamos mandando un "?" es una request GET
        String email = req.getParameter("correo");
        UserDao dao = new UserDao();
        User usuario = dao.correo(email);

        resp.sendRedirect("");
    }

    //Normalmente siempre ocupen el método doPost
    //Para información de formularios
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // el objeto req contiene toda la información
        // que proviene del js

        //Es que el método doPost haga el inicio de sesión
        String correo = req.getParameter("correo");
        // ^ esto funciona siii, el input del form
        // tiene el name = "user"


        //En este punto solo tengo los valores de los inputs
        // Me hace falta llamar a un archivo para ver si existe
        // o no el usuario en la base de datos
        UserDao dao = new UserDao();
        User usuario = dao.correo(correo);

        //Imprimir el nombre del usuario en la base de datos
        //Si no existe el usuario entonces imprime null
        System.out.println(usuario.getNombre());
        String ruta = "solicitudRecuperacion.jsp";
        HttpSession sesion = req.getSession();
        if(usuario.getNombre() == null){
            //Entonces no voy a poder iniciar sesión
            sesion.setAttribute("mensaje", "El email no existe en la base de datos");
        }else{
            //Entonces si inicie la sesión
            String cod = SimpleRandomStringGenerator.generateRandomString(256);
            UserDao codigoDao = new UserDao();
            Boolean bandera = codigoDao.updateCodigo(cod,correo);
            if(bandera){
                try {
                    GmailSender mLink = new GmailSender();
                    String linkRecu = "";
                    mLink.sendMail(correo,"Link de recuperacion", linkRecu);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                System.out.printf("el codigp se inserto corretamenta");
            }else {
                System.out.println("el codigo no se inserto corretamenta");
            }
            //sesion.setAttribute("mensaje", "Se ha enviado un codigo de recuperacion a su correo");


            //sesion.setAttribute("usuario", usuario);
        }
        resp.sendRedirect(ruta);
    }

    public void destroy() {

    }

    public void init() throws ServletException {

    }
}

