package api;

import java.net.URI;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import domain.model.Users;
import domain.service.UserService;

@ApplicationScoped
@Transactional
@Path("/user")
public class UserRestService {

	@Inject
	private UserService userservice;


	public void setUserservice(UserService us) {
		userservice = us;
	}



	@GET
	@Path("/alluser")
	@Produces("application/json")
	public List<Users> getAll() {
		return userservice.getAll();
	}

	@GET
	@Path("/getuser")
	@Produces("application/json")
	public Users getById(@QueryParam("id")String id) {
		return userservice.getByIdUser(id);
	}
	
	@POST
	@Consumes("application/json")
	public Response adduserREST(Users user1) {
		Users us = new Users(user1.getId(),user1.getName(),user1.getSurname(),user1.getEmail(), user1.getReport() );
		try {
			userservice.create(us);
		} catch(IllegalArgumentException i) {
			return Response.status(Status.BAD_REQUEST).build();
		} catch(Exception e) {
			return Response.status(Status.BAD_GATEWAY).build();
		}
		return Response.status(Status.CREATED).location(URI.create("/home")).build();
	}
	

	@PUT
	@Consumes("application/json")
	public Response incremetReport(String id,String idReport) {
		try {
			userservice.incrementReport(id,idReport);
		} catch(IllegalArgumentException i) {
			return Response.status(Status.BAD_REQUEST).build();
		} catch(Exception e) {
			return Response.status(Status.BAD_GATEWAY).build();
		}
		return Response.status(Status.CREATED).location(URI.create("/home")).build();
	}

	@PUT
	@Path("/image")
	@Consumes("application/json")
	public Response updateImage(Users newUser) {
		try {
			Users us = new Users(newUser.getId(),newUser.getName(), newUser.getSurname(),newUser.getEmail(),newUser.getReport());
			us.setImage(newUser.getImage());
			userservice.updateImage(us);
		} catch(IllegalArgumentException i) {
			return Response.status(Status.BAD_REQUEST).build();
		} catch(Exception e) {
			return Response.status(Status.BAD_GATEWAY).build();
		}
		return Response.status(Status.CREATED).location(URI.create("/home")).build();
	}



}
