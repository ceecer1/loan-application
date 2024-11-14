
### Run

mvn kalix:runAll

grpcurl -d '{"loan_app_id": {
"compIdPart1": "125",
"compIdPart2": "abc"
}, "client_id": "client100", "client_monthly_income_cents": 100000, "loan_amount_cents": 200000, "loan_duration_months": 28}' -plaintext localhost:9000 io.kx.loanapp.api.LoanAppService/Submit



grpcurl -d '{"loan_app_id": {
"compIdPart1": "125",
"compIdPart2": "abc"
}}' -plaintext localhost:9000 io.kx.loanapp.api.LoanAppService/Get
