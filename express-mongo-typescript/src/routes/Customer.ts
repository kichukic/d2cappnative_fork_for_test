import express from 'express';
import controller from '../controller/Customer';
import extractJWT from '../middleware/extractJWT';

const router = express.Router();

router.post('/customercreate', controller.customercreate);
router.get('/customerget/:customerId', controller.customerget);
router.get('/customergetall', controller.customergetall);
router.patch('/customerupdate/:customerId', controller.customerupdate);
router.delete('/customerdelete/:customerId', controller.customerdelete);
// router.post('/customerlogin', controller.customerlogin);

export = router;
