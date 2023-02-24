import express from 'express';
import controller from '../controller/Store';
import extractJWT from '../middleware/extractJWT';

const router = express.Router();

router.post('/storecreate', controller.storecreate);
router.get('/storeget/:storeId', controller.storeget);
router.get('/storegetall', controller.storegetall);
router.patch('/storeupdate/:storeId', controller.storeupdate);
router.delete('/storedelete/:storeId', controller.storedelete);

router.post('/storelogin', controller.storelogin);

// router.post('/create', ValidateJoi(Schemas.author.create), controller.createAuthor);
// router.patch('/update/:authorId', ValidateJoi(Schemas.author.update), controller.updateAuthor);

export = router;
