import express from "express";
import controller from '../controller/Agent';

const router =express.Router();

router.post('/create', controller.createAgent);
router.get('/get/:agentId', controller.getAgent);
router.get('/getallagent', controller.getAgentAll);
router.patch('/update/:agentId', controller.updateAgent);
router.delete('/delete/:agentId', controller.deleteAgent);

// router.post('/create', ValidateJoi(Schemas.author.create), controller.createAuthor);
// router.patch('/update/:authorId', ValidateJoi(Schemas.author.update), controller.updateAuthor);

export = router;