package uk.nhs.nhsx.keyfederation

import uk.nhs.nhsx.domain.BatchTag
import uk.nhs.nhsx.keyfederation.upload.lookup.UploadKeysResult
import java.time.Instant
import java.time.LocalDate
import java.util.*

class InMemoryBatchTagService(
    var batchTag: BatchTag? = null,
    var batchDate: LocalDate? = null
) : BatchTagService {

    override fun lastUploadState(): UploadKeysResult? = null
    override fun updateLastUploadState(lastUploadTimestamp: Instant) = Unit

    override fun latestFederationBatch() = when {
        batchTag != null && batchDate != null -> FederationBatch(batchTag!!, batchDate!!)
        else -> null
    }

    override fun updateLatestFederationBatch(batch: FederationBatch) {
        this.batchTag = batch.batchTag
        this.batchDate = batch.batchDate
    }

    override fun toString() = "InMemoryBatchTagService(batchTag=$batchTag, batchDate=$batchDate)"
}
