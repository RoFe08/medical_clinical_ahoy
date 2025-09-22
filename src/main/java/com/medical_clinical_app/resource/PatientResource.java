package com.medical_clinical_app.resource;

import com.medical_clinical_app.dto.patient.request.PatientCreateRequest;
import com.medical_clinical_app.dto.patient.request.PatientUpdateRequest;
import com.medical_clinical_app.dto.patient.response.PatientResponse;
import com.medical_clinical_app.model.Patient;
import com.medical_clinical_app.service.PatientService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.List;

@Path("/patients")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PatientResource {

    public PatientResource() {}

    @Inject
    PatientService patientService;

    @GET
    public List<Patient> getAllPatient(@QueryParam("page") @DefaultValue("0") int page,
                              @QueryParam("size") @DefaultValue("20") int size) {
        return patientService.listAll(page, size);
    }

    @GET
    @Path("{id}")
    public Patient getPatientById(@PathParam("id") Long id) {
        return patientService.listAll(0, 1).stream()
                .filter(p -> p.getIdPaciente().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Patient not found"));
    }

    @POST
    public Response createPatient(@Valid PatientCreateRequest dto, @Context UriInfo uriInfo) {
        PatientResponse created = patientService.create(dto);
        UriBuilder uri = uriInfo.getAbsolutePathBuilder().path(created.getUuid());
        return Response.created(uri.build())
                .entity(created)
                .build();
    }

    @PUT
    @Path("{uuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePatient(@PathParam("uuid") String uuid,
                                  @Valid PatientUpdateRequest dto) {
        PatientResponse updated = patientService.update(uuid, dto);
        return Response.ok(updated).build(); // 200 OK
    }

    @DELETE
    public void deletePatientById(String idPatient) {
        patientService.delete(idPatient);
    }

}
