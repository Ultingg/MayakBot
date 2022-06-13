package ru.kumkuat.application.GameModule.Service;


import ru.kumkuat.application.GameModule.Models.DisposablePromocode;

/**
 * Interface to service {@link DisposablePromocode} operations: creating, checking and saving
 */
public interface PromocodeService {


    /**
     * Validate promocode with database.
     * Check if poromocode exists in DB, if it's not return false.
     * If it exists, check if it was already used and return result of this check.
     * Mark existing promocaod as used and set time and date of used.
     * @param value of promocode to check
     * @return true if pormocode valid, but false if it's not or was already used.
     */
    boolean checkPromocode(String value);

    /**
     * Create new pormocode save it to DB, mark as unused.
     * @return new DisposalPormocode.
     */
    DisposablePromocode createNewDisposalPormocode();
}
