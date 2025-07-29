package in.vijay.id_generations.idgenerations;



import in.vijay.id_generations.idgenerations.model.IdSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdSequenceRepository extends JpaRepository<IdSequence, String> {}