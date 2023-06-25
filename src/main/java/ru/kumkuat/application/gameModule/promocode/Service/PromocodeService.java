package ru.kumkuat.application.gameModule.promocode.Service;


import ru.kumkuat.application.gameModule.promocode.Model.DisposablePromocode;

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

    /**
     * Get disposal promocode from db, mark it as sent.
     * If there are no codes in db creates new one mark as sent and returns it.
     * @return disposalPromocode.
     */
    DisposablePromocode getDisposalPromocode();

    /**
     * Create new pormocode save it to DB, mark as unused, but sent.
     * @return
     */
    DisposablePromocode getNewDisposalMarkedPromocode();
}
