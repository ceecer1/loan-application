package io.kx.loanapp.domain;

import com.google.protobuf.Empty;
import com.google.protobuf.util.Timestamps;
import io.kx.loanapp.api.LoanAppApi;
import kalix.javasdk.eventsourcedentity.EventSourcedEntity;
import kalix.javasdk.eventsourcedentity.EventSourcedEntity.Effect;
import kalix.javasdk.eventsourcedentity.EventSourcedEntityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This class was initially generated based on the .proto definition by Kalix tooling.
// This is the implementation for the Event Sourced Entity Service described in your io/kx/loanapp/api/loan_app_api.proto file.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class LoanAppEntity extends AbstractLoanAppEntity {

  private static Logger logger = LoggerFactory.getLogger(LoanAppEntity.class);

  @SuppressWarnings("unused")
  private final String entityId;

  public LoanAppEntity(EventSourcedEntityContext context) {
    this.entityId = context.entityId();
  }

  @Override
  public LoanAppDomain.LoanAppDomainState emptyState() {
    return LoanAppDomain.LoanAppDomainState.getDefaultInstance();
  }

  @Override
  public Effect<Empty> submit(LoanAppDomain.LoanAppDomainState currentState, LoanAppApi.SubmitCommand submitCommand) {
    //validation logic
    logger.info("Handled submit");
    if(currentState.equals(LoanAppDomain.LoanAppDomainState.getDefaultInstance())) {
      LoanAppDomain.Submitted submitted = LoanAppDomain.Submitted.newBuilder()
              .setClientId(submitCommand.getClientId())
              .setClientMonthlyIncomeCents(submitCommand.getClientMonthlyIncomeCents())
              .setLoanAmountCents(submitCommand.getLoanAmountCents())
              .setLoanDurationMonths(submitCommand.getLoanDurationMonths())
              .setLastUpdateTimestamp(Timestamps.fromMillis(System.currentTimeMillis()))
              .setLoanAppId(submitCommand.getLoanAppId())
              .build();

      return effects().emitEvent(submitted).thenReply(any -> Empty.getDefaultInstance());
    } else if(currentState.getStatus() == LoanAppDomain.LoanAppDomainStatus.STATUS_IN_REVIEW) {
      return effects().reply(Empty.getDefaultInstance());
    } else {
      return effects().error("Current state is not null, and also not in status review, so something must be wrong, " +
              "you should not be sending submit request for an existing loan app id");
    }
  }

  @Override
  public Effect<LoanAppApi.LoanAppState> get(LoanAppDomain.LoanAppDomainState currentState, LoanAppApi.GetCommand getCommand) {
    LoanAppApi.LoanAppState apiState = LoanAppApi.LoanAppState.newBuilder()
            .setClientId(currentState.getClientId())
            .setClientMonthlyIncomeCents(currentState.getClientMonthlyIncomeCents())
            .setLoanAmountCents(currentState.getLoanAmountCents())
            .setLoanDurationMonths(currentState.getLoanDurationMonths())
            .setDeclineReason(currentState.getDeclineReason())
            .setStatus(LoanAppApi.LoanAppStatus.forNumber(currentState.getStatus().getNumber()))
            .build();
    return effects().reply(apiState);
  }

  @Override
  public Effect<Empty> approve(LoanAppDomain.LoanAppDomainState currentState, LoanAppApi.ApproveCommand approveCommand) {
    return effects().error("The command handler for `Approve` is not implemented, yet");
  }

  @Override
  public Effect<Empty> decline(LoanAppDomain.LoanAppDomainState currentState, LoanAppApi.DeclineCommand declineCommand) {
    return effects().error("The command handler for `Decline` is not implemented, yet");
  }

  @Override
  public LoanAppDomain.LoanAppDomainState submitted(LoanAppDomain.LoanAppDomainState currentState, LoanAppDomain.Submitted submitted) {
    return LoanAppDomain.LoanAppDomainState.newBuilder()
            .setStatus(LoanAppDomain.LoanAppDomainStatus.STATUS_IN_REVIEW)
            .setClientId(submitted.getClientId())
            .setLoanDurationMonths(submitted.getLoanDurationMonths())
            .setLoanAmountCents(submitted.getLoanAmountCents())
            .setClientMonthlyIncomeCents(submitted.getClientMonthlyIncomeCents())
            .setLastUpdateTimestamp(submitted.getLastUpdateTimestamp())
            .build();

  }
  @Override
  public LoanAppDomain.LoanAppDomainState approved(LoanAppDomain.LoanAppDomainState currentState, LoanAppDomain.Approved approved) {
    throw new RuntimeException("The event handler for `Approved` is not implemented, yet");
  }
  @Override
  public LoanAppDomain.LoanAppDomainState declined(LoanAppDomain.LoanAppDomainState currentState, LoanAppDomain.Declined declined) {
    throw new RuntimeException("The event handler for `Declined` is not implemented, yet");
  }

}
