package fr.sqq.achatgroupe.infrastructure.in.rest.admin.resource;

import fr.sqq.achatgroupe.application.command.ApplyShortageAdjustmentsCommand;
import fr.sqq.achatgroupe.application.command.ProcessRefundsCommand;
import fr.sqq.achatgroupe.application.command.ProcessRefundsCommand.RefundSummary;
import fr.sqq.achatgroupe.application.command.RecordReceptionCommand;
import fr.sqq.achatgroupe.application.command.RecordReceptionCommand.ReceptionLineCommand;
import fr.sqq.achatgroupe.application.query.GetReceptionStatusQuery;
import fr.sqq.achatgroupe.application.query.GetReceptionStatusQuery.ReceptionStatusResult;
import fr.sqq.achatgroupe.application.query.GetRefundPreviewQuery;
import fr.sqq.achatgroupe.application.query.GetRefundPreviewQuery.RefundPreviewResult;
import fr.sqq.achatgroupe.application.query.GetShortagePreviewQuery;
import fr.sqq.achatgroupe.application.query.GetShortagePreviewQuery.ShortagePreviewResult;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.ProcessRefundsResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.RecordReceptionRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.ReceptionStatusResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.ReceptionStatusResponse.ReceptionLineResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.ReceptionStatusResponse.SupplierReceptionStatus;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.RefundPreviewResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.ShortagePreviewResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.mediator.Mediator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/admin/ventes/{venteId}/receptions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "admin-reception")
public class AdminReceptionResource {

    private final Mediator mediator;

    public AdminReceptionResource(Mediator mediator) {
        this.mediator = mediator;
    }

    @GET
    public DataResponse<ReceptionStatusResponse> getReceptionStatus(@PathParam("venteId") Long venteId) {
        ReceptionStatusResult result = mediator.send(new GetReceptionStatusQuery(venteId));

        List<SupplierReceptionStatus> suppliers = result.suppliers().stream()
                .map(s -> new SupplierReceptionStatus(
                        s.supplier(), s.receptionId(), s.status(),
                        s.lines().stream()
                                .map(l -> new ReceptionLineResponse(
                                        l.productId(), l.productName(),
                                        l.orderedQuantity(), l.receivedQuantity(), l.shortage()))
                                .toList()))
                .toList();

        return new DataResponse<>(new ReceptionStatusResponse(suppliers, result.allReceived(), result.hasRefunds()));
    }

    @POST
    public DataResponse<Void> recordReception(@PathParam("venteId") Long venteId,
                                              RecordReceptionRequest request) {
        List<ReceptionLineCommand> lines = request.lines().stream()
                .map(l -> new ReceptionLineCommand(l.productId(), l.receivedQuantity()))
                .toList();

        mediator.send(new RecordReceptionCommand(venteId, request.supplier(), lines));
        return new DataResponse<>(null);
    }

    @GET
    @Path("/shortages")
    public DataResponse<ShortagePreviewResponse> getShortagePreview(@PathParam("venteId") Long venteId) {
        ShortagePreviewResult result = mediator.send(new GetShortagePreviewQuery(venteId));

        List<ShortagePreviewResponse.ShortageItem> items = result.items().stream()
                .map(i -> new ShortagePreviewResponse.ShortageItem(
                        i.productId(), i.productName(), i.supplier(),
                        i.orderedQty(), i.receivedQty(), i.shortage(), i.affectedOrderCount()))
                .toList();

        return new DataResponse<>(new ShortagePreviewResponse(items));
    }

    @POST
    @Path("/adjustments")
    public DataResponse<Void> applyAdjustments(@PathParam("venteId") Long venteId) {
        mediator.send(new ApplyShortageAdjustmentsCommand(venteId));
        return new DataResponse<>(null);
    }

    @GET
    @Path("/refunds")
    public DataResponse<RefundPreviewResponse> getRefundPreview(@PathParam("venteId") Long venteId) {
        RefundPreviewResult result = mediator.send(new GetRefundPreviewQuery(venteId));

        List<RefundPreviewResponse.RefundOrderItem> orders = result.orders().stream()
                .map(o -> new RefundPreviewResponse.RefundOrderItem(
                        o.orderId(), o.orderNumber(), o.customerName(),
                        o.originalAmount().amount(), o.adjustedAmount().amount(), o.refundAmount().amount(), o.refundStatus()))
                .toList();

        return new DataResponse<>(new RefundPreviewResponse(orders));
    }

    @POST
    @Path("/refunds")
    public DataResponse<ProcessRefundsResponse> processRefunds(@PathParam("venteId") Long venteId) {
        RefundSummary summary = mediator.send(new ProcessRefundsCommand(venteId));
        return new DataResponse<>(new ProcessRefundsResponse(
                summary.totalProcessed(), summary.totalSucceeded(),
                summary.totalFailed(), summary.totalAmountRefunded().amount()));
    }
}
