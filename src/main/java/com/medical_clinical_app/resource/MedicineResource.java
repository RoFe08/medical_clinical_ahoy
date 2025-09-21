package com.medical_clinical_app.resource;

import com.medical_clinical_app.dto.medicine.request.MedicineCreateRequest;
import com.medical_clinical_app.dto.medicine.response.MedicineResponse;
import com.medical_clinical_app.dto.medicine.request.MedicineUpdateRequest;
import com.medical_clinical_app.service.MedicineService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.net.URI;
import java.util.List;

@Path("/medicines")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MedicineResource {

    public MedicineResource() {}

    @Inject
    MedicineService medicineService;

    @POST
    public Response createMedicine(MedicineCreateRequest req, @Context UriInfo uriInfo) {
        if (req == null || req.nome == null || req.nome.isBlank()) {
            throw new BadRequestException("name is required");
        }
        MedicineResponse created = medicineService.create(req);
        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(created.idMedicamento)).build();
        return Response.created(location).entity(created).build();
    }

    @GET
    public Response getAllMedicine(@QueryParam("name") String name,
                         @QueryParam("page") @DefaultValue("0") int page,
                         @QueryParam("size") @DefaultValue("20") int size) {
        size = Math.min(Math.max(size, 1), 100);
        List<MedicineResponse> items = medicineService.list(name, page, size);
        return Response.ok(items).build();
    }

    @GET
    @Path("{id}")
    public Response getMedicineById(@PathParam("id") Long id) {
        MedicineResponse r = medicineService.findById(id);
        if (r == null) throw new NotFoundException("medicine not found");
        return Response.ok(r).build();
    }

    @PUT
    @Path("{id}")
    public Response updateMedicineById(@PathParam("id") String id, MedicineUpdateRequest req) {
        MedicineResponse r = medicineService.updateByUuid(id, req);
        if (r == null) throw new NotFoundException("medicine not found");
        return Response.ok(r).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteMedicineById(@PathParam("id") Long id) {
        boolean ok = medicineService.delete(id);
        if (!ok) throw new NotFoundException("medicine not found");
        return Response.noContent().build();
    }
}
