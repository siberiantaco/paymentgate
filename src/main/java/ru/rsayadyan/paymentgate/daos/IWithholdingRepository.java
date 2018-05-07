package ru.rsayadyan.paymentgate.daos;

import ru.rsayadyan.paymentgate.daos.model.Withholding;

public interface IWithholdingRepository {

    public void save(Withholding withholding);

    public void delete(Withholding withholding);

    public Withholding get(String holdId);
}
